package com.hems.project.Virtual_Power_Plant.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.hems.project.Virtual_Power_Plant.dto.SupabaseObjectDto;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    private final RestTemplate restTemplate;


    //TODO:-
    //here implement Async and CompletableFuture concept km ke upload image thaya kare pachad backgroun ma
    //other execution not wait for that
    //Same do for email service also
    //note:-
    //supabase eni storage api https://PROJECT_ID.supabase.co/storage/v1/object
    //aa rete expose kare.. so aa /storage/v1/object lakhvu jarur che pachi 
    //je apde lakhvu hoy e 

    public String uploadImage(UUID vppId,MultipartFile multipartFile) throws IOException{
        String originalFilename = multipartFile.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        //TODO:-
        //for file name we take the UUID for that vpp from incoming request
        //and see from this one request user can upload multiple file ??
        String fileName=UUID.randomUUID()+"_"+originalFilename;
        String uploadUrl= supabaseUrl + "/storage/v1/object/"+bucket+ "/"+vppId+ "/" +fileName;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+supabaseKey);
        headers.setContentType(MediaType.parseMediaType(multipartFile.getContentType()));

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(multipartFile.getBytes(), headers);

        restTemplate.exchange(
            uploadUrl,
            HttpMethod.PUT,
            requestEntity,
            String.class);
        
            return supabaseUrl + "/storage/v1/object/public/"+ bucket + "/" + vppId + "/" + fileName; 
       }

       //fetch image url:-
       public String getPublicImageUrl(UUID vppId,String fileName){
            return supabaseUrl+ "/storage/v1/object/public/"+bucket+"/"+vppId+"/"+fileName;
       }


       public List<Map<String,String>> getAllImagesFromVppId(UUID vppId){
            String listUrl=supabaseUrl+"/storage/v1/object/list/"+ bucket;
             HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(supabaseKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
            "prefix", vppId.toString() + "/",
            "limit", 100,
            "offset", 0
            );

    HttpEntity<Map<String, Object>> request =
            new HttpEntity<>(body, headers);

    ResponseEntity<SupabaseObjectDto[]> response =
            restTemplate.exchange(
                    listUrl,
                    HttpMethod.POST,
                    request,
                    SupabaseObjectDto[].class
            );

    if (response.getBody() == null) {
        return List.of();
    }

    return Arrays.stream(response.getBody())
            .map(obj -> Map.of(
                    "fileName", obj.getName(),
                    "url", getPublicImageUrl(vppId, obj.getName())
            ))
            .toList();
       }

    public void deleteAllFilesByVppId(UUID vppId) {

        List<Map<String, String>> files = getAllImagesFromVppId(vppId);
        if (files.isEmpty()) return;

        List<String> prefixes = files.stream()
                .map(m -> vppId + "/" + m.get("fileName"))
                .toList();

        //bulk delete karva mate
        String deleteUrl = supabaseUrl + "/storage/v1/object/" + bucket;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(supabaseKey);
        headers.set("apikey", supabaseKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of("prefixes", prefixes);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.exchange(
                deleteUrl,
                HttpMethod.DELETE,
                request,
                String.class
        );
    }



    public void deleteFiles(UUID vppId, List<String> fileNames) {

        if (fileNames == null || fileNames.isEmpty()) return;

        List<String> prefixes = fileNames.stream()
                .map(name -> vppId + "/" + name)
                .toList();

        String url = supabaseUrl + "/storage/v1/object/" + bucket.trim();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("apikey", supabaseKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> req =
                new HttpEntity<>(Map.of("prefixes", prefixes), headers);

        restTemplate.exchange(url, HttpMethod.DELETE, req, String.class);
    }






}