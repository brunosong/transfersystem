package com.brunosong.transfersystem.aiservice.infrastructure.chapquestion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapQuestionId implements Serializable {

    Long chapQuestionSeq;

    Long chap;

}
