package com.drzinks.controller;

import com.drzinks.dto.RepositoryDetailsDTO;
import com.drzinks.service.RepositoryDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repositories")
@Validated
public class RepositoryDetailsController {

    @Autowired
    private RepositoryDetailsService repositoryDetailsService;

    @RequestMapping(method = RequestMethod.GET, value = "/{owner}/{repositoryName}")
    public RepositoryDetailsDTO getRepositoryDetails(@PathVariable(value = "owner") String ownerName,
                                                     @PathVariable(value = "repositoryName") String repositoryName ){
        return repositoryDetailsService.getRepositoryDetails(ownerName, repositoryName);
    }

}
