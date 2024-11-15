package com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.goods.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@IdClass(GoodsContsId.class)
@Table(name = "tb_goods_conts_meta")
public class GoodsContsMetaEntity extends DateBaseEntity {

    @Id
    @Column(name = "gc_seq")
    private Long gcSeq;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_seq")
    private GoodsMetaMasterEntity goodsMetaMaster;

    @Enumerated(value = EnumType.STRING)
    private UseYnEnum useYn; //'사용여부'

    private Long itemSeq;

    private String metaValue;

}
