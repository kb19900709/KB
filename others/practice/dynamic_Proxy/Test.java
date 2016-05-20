package com.test;

public class Test{
	
	public static void main(String[] args) {
		Music pinao = MusicProvider.getPinao();
		pinao.execute();
		
		Music speaker = MusicProvider.getSpeaker();
		speaker.execute();
	}

}
