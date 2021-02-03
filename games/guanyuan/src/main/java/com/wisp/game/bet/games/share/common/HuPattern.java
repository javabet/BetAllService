package com.wisp.game.bet.games.share.common;

import com.wisp.game.bet.games.share.enums.HuTypeEnum;

public class HuPattern {

	private int fan;
	
	private HuTypeEnum pattern;
	
	private int pai;
	
	public int getFan() {
		return fan;
	}

	public void setFan(int fan) {
		this.fan = fan;
	}

	public HuTypeEnum getPattern() {
		return pattern;
	}

	public void setPattern(HuTypeEnum pattern) {
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
