package com.cluit.util.structures;

public class Pair <L, R> {
	public L left;
	public R right;
	
	public Pair(L l, R r){
		this.left = l;
		this.right = r;
	}
	
	public String toString(){
		return "["+left+":"+right+"]";
	}
}
