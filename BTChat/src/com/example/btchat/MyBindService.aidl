package com.example.btchat;

import com.example.btchat.MyObserver;

interface MyBindService{
	void send(String message);
	void register(MyObserver observer);
}