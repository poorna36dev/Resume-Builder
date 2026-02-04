package com.svu.resume.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileUploadService{
    
    private final Cloudinary cloudinary;

    public Map<String,String> uploadSingleImage(MultipartFile file) throws IOException{
       Map<String,Object>imageUploadResult= cloudinary.uploader().upload(file.getBytes(),ObjectUtils.asMap("resource_type","image"));
       String result=imageUploadResult.get("secure_url").toString();
       return Map.of("imageUrl",result);

    }
}
