package com.dnastack.dos.registry.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class servers as ...
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@RestController
public class DemoBearerOnlyController {

    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public String hello() {
        return "world";
    }
}
