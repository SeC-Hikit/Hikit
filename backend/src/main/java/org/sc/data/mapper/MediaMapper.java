package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.FileDetailsDto;
import org.sc.common.rest.MediaDto;
import org.sc.data.model.FileDetails;
import org.sc.data.model.Media;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MediaMapper {
    default MediaDto mediaToDto(final Media media) {
        final FileDetails recordDetails = media.getFileDetails();
        return new MediaDto(media.getCreationDate(), media.getId(), media.getName(),
                media.getFileName(), media.getFileUrl(),
                media.getResolutions(),
                media.getMime(), media.getFileSize(),
                new FileDetailsDto(recordDetails.getUploadedOn(), recordDetails.getUploadedBy(),
                recordDetails.getOnInstance(), recordDetails.getRealm(), recordDetails.getFilename(),
                        recordDetails.getOriginalFilename(), recordDetails.getLastModifiedBy())
        );
    }

    ;
}
