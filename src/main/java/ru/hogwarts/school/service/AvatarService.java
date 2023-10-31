package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarService {

    private final String avatarDir;
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

     private final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    public AvatarService(AvatarRepository avatarRepository,
                         StudentRepository studentRepository,
                         @Value("${path.to.avatars.folder}") String avatarDir) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
        this.avatarDir = avatarDir;
    }

    public void uploadAvatar(Long studentId,
                             MultipartFile avatarFile) throws IOException {
        logger.info("A method was called to save the avatar for DB");
        Student student = studentRepository.getById(studentId);

        Path filePath = Path.of(avatarDir, studentId + "." + getExtensions(avatarFile.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (
                InputStream is = avatarFile.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }
        Avatar avatar = findAvatar(studentId);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(generateImagePreview(filePath));

        avatarRepository.save(avatar);
    }

    private byte[] generateImagePreview(Path filePath)throws IOException {
        logger.info("The method for saving a small copy of the avatar is called");
        try (
                InputStream is = Files.newInputStream(filePath);
                BufferedInputStream bis = new BufferedInputStream(is,1024);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);

            int height = image.getHeight()/(image.getWidth()/100);
            BufferedImage preview = new BufferedImage(100,height,image.getType());
            Graphics2D graphics2D = preview.createGraphics();
            graphics2D.drawImage(image,0,0,100,height,null);
            graphics2D.dispose();

            ImageIO.write(preview,getExtensions(filePath.getFileName().toString()),baos);
            return baos.toByteArray();
        }
    }

    public List<Avatar> getAllAvatarStudentPage(Integer numberPage, Integer numberSize) {
        logger.info("The method for displaying avatars by page is called");
        PageRequest pageRequest = PageRequest.of(numberPage - 1, numberSize);
        return avatarRepository.findAll(pageRequest).getContent();
    }

    private String getExtensions(String stringFileName) {
        logger.info("Called method to get file extension");
        return stringFileName.substring(stringFileName.lastIndexOf(".") + 1);
    }

    public Avatar findAvatar(Long studentId) {
        logger.info("Called method for getting avatar");
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }

}
