package com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.goods.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@SuperBuilder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_goods_meta")
public class GoodsMetaEntity extends DateBaseEntity {

    @Id
    @Column(name = "master_seq")
    private Long masterSeq;

    @Column(name = "goods_seq")
    private Long goodsSeq;

    @Column(name = "item_seq")
    private Long itemSeq;

    @Column(name = "meta_value")
    private String metaValue;

    @Column(name = "use_yn")
    private String useYn;

}
