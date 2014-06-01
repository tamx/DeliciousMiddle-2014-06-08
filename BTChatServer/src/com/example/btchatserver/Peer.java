package com.example.btchatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import android.bluetooth.BluetoothSocket;

public class Peer implements Runnable {
	protected static MainActivity activity = null;

	private static ArrayList<Peer> list = new ArrayList<Peer>();

	private BluetoothSocket socket = null;

	public Peer(BluetoothSocket socket) {
		this.socket = socket;
		list.add(this);
		new Thread(this).start();
	}

	private void send(String message) {
		try {
			OutputStream os = this.socket.getOutputStream();
			os.write((message + "\n").getBytes());
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			InputStream is = this.socket.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				activity.update(line);
				for (Peer peer : list) {
					if (peer != this) {
						peer.send(line);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			list.remove(this);
		}
	}

}
