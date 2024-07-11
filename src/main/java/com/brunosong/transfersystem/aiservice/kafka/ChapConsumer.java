package com.brunosong.transfersystem.aiservice.kafka;

//@Slf4j
//@Component
//@RequiredArgsConstructor
//@ConditionalOnProperty(value = "spring.kafka.enabled" , havingValue = "true")
public class ChapConsumer {

//    private final ChapMapper chapMapper;
//
//    private final ChapService chapService;
//
//    @KafkaListener(topics = "bruno_chap_topic", groupId = "bruno_chap_topic_group")
//    private void listen(ChapSaveDto chapSaveDto) {
//        List<ChapDepdcRltsSaveDto> change = change(kafkaSendDto);
//        process(change);
//    }
//
//    @KafkaListener(topics = "tb_cmn_chap_depdc_rlts_dev", groupId = "tb_cmn_chap_depdc_rlts_dev_group")
//    private void listenDev(KafkaSendDto kafkaSendDto) {
//        List<ChapDepdcRltsSaveDto> change = change(kafkaSendDto);
//        process(change);
//    }
//
//    /* 변환처리 */
//    public List<ChapDepdcRltsSaveDto> change(KafkaSendDto kafkaSendDto) {
//
//        List<TranVo> tranVoList = kafkaSendDto.getTranVoList();
//        List<ChapDepdcRltsSaveDto> dtoList = new ArrayList<>();
//
//        for(TranVo tranVo : tranVoList){
//            ChapDepdcRltsSaveDto dto = chapMapper.toChapDepdcRltsSaveDto(tranVo);
//            dtoList.add(dto);
//        }
//
//        return dtoList;
//    }
//
//    /* 저장로직 */
//    public void process(List<ChapDepdcRltsSaveDto> dtoList) {
//        for (ChapDepdcRltsSaveDto chapDepdcRltsSaveDto : dtoList) {
//            chapDepdcRltsService.save(chapDepdcRltsSaveDto);
//        }
//    }


}
