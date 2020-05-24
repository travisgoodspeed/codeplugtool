package com.kk4vcz.codeplug;

public interface ImageRadio extends Radio {
	byte[] readImage();
	void writeImage(byte[] image);
}
