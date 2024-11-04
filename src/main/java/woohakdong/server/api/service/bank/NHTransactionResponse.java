package woohakdong.server.api.service.bank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NHTransactionResponse {

    @JsonProperty("Header")
    private Header header;

    @JsonProperty("TotCnt")
    private String totCnt;

    @JsonProperty("Iqtcnt")
    private String iqtcnt;

    @JsonProperty("CtntDataYn")
    private String ctntDataYn;

    @JsonProperty("REC")
    private List<Record> rec;

    @Data
    public static class Header {

        @JsonProperty("Trtm")
        private String trtm;

        @JsonProperty("Rsms")
        private String rsms;

        @JsonProperty("ApiNm")
        private String apiNm;

        @JsonProperty("IsTuno")
        private String isTuno;

        @JsonProperty("Tsymd")
        private String tsymd;

        @JsonProperty("FintechApsno")
        private String fintechApsno;

        @JsonProperty("Iscd")
        private String iscd;

        @JsonProperty("Rpcd")
        private String rpcd;

        @JsonProperty("ApiSvcCd")
        private String apiSvcCd;
    }

    @Data
    public static class Record {

        @JsonProperty("AftrBlnc")
        private String aftrBlnc;

        @JsonProperty("TrnsAfAcntBlncSmblCd")
        private String trnsAfAcntBlncSmblCd;

        @JsonProperty("BnprCntn")
        private String bnprCntn;

        @JsonProperty("Txtm")
        private String txtm;

        @JsonProperty("Tuno")
        private String tuno;

        @JsonProperty("Trdd")
        private String trdd;

        @JsonProperty("Smr")
        private String smr;

        @JsonProperty("Ccyn")
        private String ccyn;

        @JsonProperty("MnrcDrotDsnc")
        private String mnrcDrotDsnc;

        @JsonProperty("Tram")
        private String tram;

        @JsonProperty("HnisCd")
        private String hnisCd;

        @JsonProperty("HnbrCd")
        private String hnbrCd;
    }
}
