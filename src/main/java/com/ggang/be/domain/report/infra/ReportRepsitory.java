package com.ggang.be.domain.report.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ggang.be.domain.report.ReportEntity;

public interface ReportRepsitory extends JpaRepository <ReportEntity, Long>{
}
