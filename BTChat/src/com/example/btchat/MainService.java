package com.example.btchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

public class MainService extends Service implements Runnable {
	private BluetoothSocket socket = null;
	private MyObserver observer = null;

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

		@Override
		public void register(MyObserver observer) throws RemoteException {
			MainService.this.observer = observer;
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

	private Handler handler = new Handler();

	private void update(final String message) {
		this.handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					MainService.this.observer.update(message);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
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
			InputStream is = this.socket.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				update(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
