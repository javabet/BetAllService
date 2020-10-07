package com.wisp.game.bet.games.share.common;

public class HuPattern {
	public final static String NORMAL = "normal";
	public final static String SEVENN = "sevenPair";
	public final static String DUIDUIHU = "duiDuihu";
	
	
	
	public final static String L_SEVEN = "l7pairs";
	public final static String J_SEVEN = "j7pairs";
	public final static String JIAN_DUI = "jiandui";
	
	private int fan;
	
	private String pattern;
	
	private int pai;
	
	public int getFan() {
		return fan;
	}

	public void setFan(int fan) {
		this.fan = fan;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public int getPai() {
		return pai;
	}

	public void setPai(int pai) {
		this.pai = pai;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		HuPattern newPattern = (HuPattern) obj;
		
		if(newPattern.getPai() != pai)
		{
			return false;
		}
		
		if(!newPattern.getPattern().equals(pattern))
		{
			return false;
		}
		
		
		return true;
	}
	
}
