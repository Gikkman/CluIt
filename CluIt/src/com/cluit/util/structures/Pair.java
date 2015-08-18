package com.cluit.util.structures;

public class Pair <L, R> {
	public L l;
	public R r;
	
	public Pair(L l, R r){
		this.l = l;
		this.r = r;
	}
	
	public String toString(){
		return "["+l+":"+r+"]";
	}
}
