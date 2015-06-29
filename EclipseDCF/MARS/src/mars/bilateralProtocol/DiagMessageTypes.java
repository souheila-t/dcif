package mars.bilateralProtocol;

public interface DiagMessageTypes {
	public static final int DGM_PROPOSE=1;
	public static final int DGM_ACCEPT=2;
	public static final int DGM_WITHDRAW=3;
	public static final int DGM_DENY=4;
	public static final int DGM_COUNTEREXAMPLE=5;
	public static final int DGM_COUNTEREXAMPLE_COMP=11;
	public static final int DGM_COUNTEREXAMPLE_COH=12;
	public static final int DGM_CHALLENGE=6;
	public static final int DGM_ARGUE=7;
	
	public static final int DGM_ASK=8;
	public static final int DGM_INFORM=9;
	public static final int DGM_ACK=10;	
	public static final int DGM_ACK_INFORM=20;
	
	public static final int DGM_CHCK_CTXT=13;
	public static final int DGM_CHCK_CTXT_FRST=16;
	public static final int DGM_CONFIRM_CTXT=14;
	public static final int DGM_ACK_CONFIRM=15;	
	public static final int DGM_WITHDRAW_INCOMPLETE=17;
	public static final int DGM_ASK_OTHERHYP=18;
	public static final int DGM_PROPOSEAGAIN=19;
	public static final int DGM_HASBETTERHYP=21;
	
}
