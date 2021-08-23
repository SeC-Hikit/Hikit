package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.configuration.AppProperties;
import org.sc.data.model.Trail;
import org.sc.processor.pdf.PdfFileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

import static org.sc.controller.admin.Constants.PREFIX_TEST;

@RestController
@RequestMapping(PREFIX_TEST)
public class AdminTestController {

    private final PdfFileHelper pdfFileHelper;
    private final AppProperties appProperties;

    @Autowired
    public AdminTestController(final PdfFileHelper pdfFileHelper,
                               final AppProperties appProperties) {
        this.pdfFileHelper = pdfFileHelper;
        this.appProperties = appProperties;
    }

    @Operation(summary = "Test admin")
    @GetMapping
    public String test() {
        pdfFileHelper.exportPdf(Trail.builder().build(), new File(appProperties.getStorage() + "/abc.pdf").toPath());
        return "A";
    }
}
