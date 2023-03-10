package clone.gozik.S3;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ArrayList<String> upload(MultipartFile multipartFile, String dirName){

        try{
            File uploadFile = convert(multipartFile)
                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패try줄"));
            return upload(uploadFile,dirName);
        }catch (IOException e){
            throw new IllegalArgumentException("MultipartFile -> File 전환 실패catch줄");
        }

    }

    public Optional<File> convert(MultipartFile file) throws IOException{
        File convertFile = new File(file.getOriginalFilename());
        if (convertFile.createNewFile()){
            try (FileOutputStream fos = new FileOutputStream(convertFile)){
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    private ArrayList<String> upload(File uploadFile, String dirName){

        String fileName = dirName +"/" +uploadFile.getName() + LocalDateTime.now();
        log.info("key : "+fileName);

        String uploadImageUrl = putS3(uploadFile,fileName);
        log.info("image_url : " + uploadImageUrl);

        removeNewFile(uploadFile);

        ArrayList<String> results = new ArrayList<>();
        results.add(fileName);
        results.add(uploadImageUrl);
        return results;
    }

    private String putS3(File uploadFile, String fileName){
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        return amazonS3Client.getUrl(bucket,fileName).toString();
    }

    private void removeNewFile(File targetFile){
        if (targetFile.delete()){
            log.info("파일이 삭제되었습니다.");
        }else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

}
