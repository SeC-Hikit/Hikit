package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.sc.controller.admin.Constants.PREFIX_TEST;

@RestController
@RequestMapping(PREFIX_TEST)
public class AdminTestController {

    @Operation(summary = "Test admin")
    @GetMapping
    public String test() {

        return "{'Hello':'Bello'}";
    }
}
