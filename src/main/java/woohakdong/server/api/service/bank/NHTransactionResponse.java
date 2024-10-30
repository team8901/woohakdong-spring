package woohakdong.server.api.service.bank;

import lombok.Data;

import java.util.List;

@Data
public class NHTransactionResponse {
    private Header Header;
    private String TotCnt;
    private String Iqtcnt;
    private String CtntDataYn;
    private List<Record> REC;

    @Data
    public static class Header {
        private String Trtm;
        private String Rsms;
        private String ApiNm;
        private String IsTuno;
        private String Tsymd;
        private String FintechApsno;
        private String Iscd;
        private String Rpcd;
        private String ApiSvcCd;
    }

    @Data
    public static class Record {
        private String AftrBlnc;
        private String TrnsAfAcntBlncSmblCd;
        private String BnprCntn;
        private String Txtm;
        private String Tuno;
        private String Trdd;
        private String Smr;
        private String Ccyn;
        private String MnrcDrotDsnc;
        private String Tram;
        private String HnisCd;
        private String HnbrCd;
    }
}
