package com.rohit.ai_job_board.mapper;


import com.rohit.ai_job_board.dto.request.CreateJobRequest;
import com.rohit.ai_job_board.dto.response.JobResponse;
import com.rohit.ai_job_board.entity.Jobs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface JobMapper {

    Jobs toEntity(CreateJobRequest request);

    @Mapping(target = "recruiterName",
            expression = "java(job.getRecruiter().getFirstName() + \" \" + job.getRecruiter().getLastName())")
    JobResponse toResponse(Jobs job);

}
