package org.sc.controller;

import org.sc.common.rest.InstanceInfoDto;
import org.sc.configuration.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(InstanceController.PREFIX)
public class InstanceController {

    public final static String PREFIX = "/instance";

    private final AppProperties appProperties;

    @Autowired
    public InstanceController(final AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping
    public InstanceInfoDto get() {
        return InstanceInfoDto.builder()
                .realm(appProperties.getInstanceRealm())
                .runningVersion(AppProperties.DISPLAYED_VERSION)
                .instance(appProperties.getInstanceId())
                .build();
    }

}
