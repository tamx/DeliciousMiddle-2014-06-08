package com.example.btchat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class MainService extends Service implements Runnable {
	private BluetoothSocket socket = null;

	private final MyBindService.Stub mMyBindService = new MyBindService.Stub() {
		@Override
		public void send(String message) throws RemoteException {
			try {
				OutputStream os = MainService.this.socket.getOutputStream();
				os.write((message + "\n").getBytes());
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return mMyBindService;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			// Unique UUID for this application
			UUID uuid = UUID.fromString("00000000-1111-1111-0000-111111111111");
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			BluetoothDevice device = adapter
					.getRemoteDevice("78:1C:5A:D1:BD:72");
			this.socket = device
					.createInsecureRfcommSocketToServiceRecord(uuid);
			this.socket.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
