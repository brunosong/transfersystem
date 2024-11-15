package com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.goods.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_goods_meta_master")
public class GoodsMetaMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "master_seq")
    private Long masterSeq;

    @Builder.Default
    @OneToMany(mappedBy = "metaMaster", fetch = FetchType.LAZY)
    private List<GoodsMetaItemEntity> metaItemList = new ArrayList<>();

    @Column(name = "meta_name")
    private String metaName; // '메타명'

    @Column(name = "meta_code")
    private String metaCode; // '코드'

    @Column(name = "input_type")
    private String inputType; //'html input type'

    @ColumnDefault(value = "'Y'")
    @Enumerated(EnumType.STRING)
    @Column(name = "is_goods")
    private UseYnEnum isGoods; // '자재 마스터 여부(N이면 콘텐츠 마스터)'

    @ColumnDefault(value = "'Y'")
    @Column(name = "conts_gubun")
    private String contsGubun; // '콘텐츠 구분'

    private Integer orders; //'순서'

    @ColumnDefault(value = "'N'")
    @Enumerated(EnumType.STRING)
    @Column(name = "must_save_yn")
    private UseYnEnum mustSaveYn;  //'필수저장여부'

    @ColumnDefault(value = "'Y'")
    @Enumerated(EnumType.STRING)
    @Column(name = "use_yn")
    private UseYnEnum useYn; //'사용여부'

    @ColumnDefault(value = "'N'")
    @Enumerated(EnumType.STRING)
    @Column(name = "base_yn")
    private UseYnEnum baseYn;  //'기본속성여부'

    @ColumnDefault(value = "'N'")
    @Enumerated(EnumType.STRING)
    @Column(name = "auto_yn")
    private UseYnEnum autoYn; // '자동생성여부'

    @ColumnDefault(value = "'N'")
    @Enumerated(EnumType.STRING)
    @Column(name = "display_yn")
    private UseYnEnum displayYn; //'자재등록 화면 노출여부'

    public void addGoodsMetaItemEntity(GoodsMetaItemEntity goodsMetaItemEntity){
        metaItemList.add(goodsMetaItemEntity);
    }

    @CreationTimestamp
    @Column(name = "cre_dtime")
    private LocalDateTime creDtime;

    @Column(name = "cre_id")
    private String creId;

    @Column(name = "cre_ip")
    private String creIp;

    @UpdateTimestamp
    @Column(name = "upd_dtime")
    private LocalDateTime updDtime;

    @Column(name = "upd_ip")
    private String updIp;

    @Column(name = "upd_id")
    private String updId;


}

