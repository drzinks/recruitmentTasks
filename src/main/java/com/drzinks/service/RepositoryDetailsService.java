package com.drzinks.service;

import com.drzinks.dto.RepositoryDetailsDTO;
import com.drzinks.exception.RestTemplateResponseErrorHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RepositoryDetailsService {

    private RestTemplate restTemplate;
    private static final String uri ="https://api.github.com/repos/";

    public RepositoryDetailsDTO getRepositoryDetails(String ownerName, String repositoryName){
        restTemplate = new RestTemplateBuilder().errorHandler(new RestTemplateResponseErrorHandler().setPath("/"+ownerName+"/"+repositoryName)).build();
        System.out.println("fsd");
        System.out.println("fsd2");
        ResponseEntity<RepositoryDetailsDTO> entity = restTemplate.getForEntity(uri+ownerName+"/"+repositoryName,RepositoryDetailsDTO.class);
        return entity.getBody();
    }

}
