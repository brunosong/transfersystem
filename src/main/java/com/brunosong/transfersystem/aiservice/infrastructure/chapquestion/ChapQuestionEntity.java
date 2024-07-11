package com.brunosong.transfersystem.aiservice.infrastructure.chapquestion;

import com.brunosong.transfersystem.aiservice.infrastructure.chap.ChapEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "bruno_chap_question")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@IdClass(ChapQuestionId.class)
public class ChapQuestionEntity {

    @Id
    @Column(name = "chap_question_seq")
    Long chapQuestionSeq;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chap_seq")
    private ChapEntity chap;


}
