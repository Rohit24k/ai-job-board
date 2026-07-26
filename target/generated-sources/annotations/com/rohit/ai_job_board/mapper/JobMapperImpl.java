package com.rohit.ai_job_board.mapper;

import com.rohit.ai_job_board.dto.request.CreateJobRequest;
import com.rohit.ai_job_board.dto.response.JobResponse;
import com.rohit.ai_job_board.entity.Jobs;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-26T17:56:35+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class JobMapperImpl implements JobMapper {

    @Override
    public Jobs toEntity(CreateJobRequest request) {
        if ( request == null ) {
            return null;
        }

        Jobs.JobsBuilder jobs = Jobs.builder();

        jobs.title( request.getTitle() );
        jobs.description( request.getDescription() );
        jobs.requiredSkills( request.getRequiredSkills() );
        jobs.experience( request.getExperience() );
        jobs.location( request.getLocation() );
        jobs.salary( request.getSalary() );
        jobs.jobType( request.getJobType() );

        return jobs.build();
    }

    @Override
    public JobResponse toResponse(Jobs job) {
        if ( job == null ) {
            return null;
        }

        JobResponse.JobResponseBuilder jobResponse = JobResponse.builder();

        jobResponse.id( job.getId() );
        jobResponse.title( job.getTitle() );
        jobResponse.description( job.getDescription() );
        jobResponse.requiredSkills( job.getRequiredSkills() );
        jobResponse.experience( job.getExperience() );
        jobResponse.location( job.getLocation() );
        jobResponse.salary( job.getSalary() );
        jobResponse.jobType( job.getJobType() );
        jobResponse.status( job.getStatus() );
        jobResponse.createdAt( job.getCreatedAt() );

        jobResponse.recruiterName( job.getRecruiter().getFirstName() + " " + job.getRecruiter().getLastName() );

        return jobResponse.build();
    }
}
