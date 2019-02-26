package com.jtudy.git.measure.domain;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class MeasureParameters {
	
	private Date fromDate;
	private Date toDate;
}
