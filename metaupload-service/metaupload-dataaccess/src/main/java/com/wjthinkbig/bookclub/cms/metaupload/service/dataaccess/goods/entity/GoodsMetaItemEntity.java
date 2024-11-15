package com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.goods.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "tb_goods_meta_item")
public class GoodsMetaItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_seq")
    private Long itemSeq;

    @NotNull
    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_code")
    private String itemCode;  // '항목코드'

    private Long orders;  //'순서'

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_seq")
    private GoodsMetaMasterEntity metaMaster;

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

    @ColumnDefault(value = "'Y'")
    @Enumerated(EnumType.STRING)
    @Column(name = "use_yn")
    private UseYnEnum useYn; //'사용여부'

    @Builder
    public GoodsMetaItemEntity(Long itemSeq, String itemName, String itemCode, Long orders,
                               GoodsMetaMasterEntity metaMaster, LocalDateTime creDtime, String creId, String creIp, LocalDateTime updDtime, String updIp, String updId, UseYnEnum useYn) {
        this.itemSeq = itemSeq;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.orders = orders;
        this.metaMaster = metaMaster;
        this.creDtime = creDtime;
        this.creId = creId;
        this.creIp = creIp;
        this.updDtime = updDtime;
        this.updIp = updIp;
        this.updId = updId;
        this.useYn = useYn;
    }
}

