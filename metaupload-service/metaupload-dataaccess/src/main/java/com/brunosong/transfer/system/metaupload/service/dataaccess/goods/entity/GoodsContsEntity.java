package com.brunosong.transfer.system.metaupload.service.dataaccess.goods.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tb_goods_conts")
public class GoodsContsEntity extends DateBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gc_seq")
    private Long gcSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_seq")
    private GoodsMetaMasterEntity goodsMetaMaster;

    @Builder.Default
    @OneToMany(mappedBy = "gcSeq", fetch = FetchType.LAZY)
    private List<GoodsContsMetaEntity> goodsContsMetaEntityList = new ArrayList<>();

    private Long goodsSeq;

    private Long contentsSeq;

    private String contsName;

    private String contsType;

    private Integer orders;

    @Enumerated(value = EnumType.STRING)
    private UseYnEnum useYn; //'사용여부'

    public void addGoodsContsMeta(GoodsContsMetaEntity goodsContsMetaEntity){
        goodsContsMetaEntityList.add(goodsContsMetaEntity);
    }

}
