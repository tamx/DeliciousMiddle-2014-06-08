package com.example.btchatserver;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements Runnable {
	private Handler handler = new Handler();
	private BluetoothServerSocket ssocket = null;
	private TextView textview = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		Peer.activity = this;
		new Thread(this).start();
	}

	@Override
	protected void onDestroy() {
		try {
			this.ssocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.ssocket = null;
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			((MainActivity) getActivity()).textview = (TextView) rootView
					.findViewById(R.id.textView1);
			return rootView;
		}
	}

	@Override
	public void run() {
		// Unique UUID for this application
		UUID uuid = UUID.fromString("00000000-1111-1111-0000-111111111111");
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		System.err.println("BT Address: " + adapter.getAddress());
		try {
			if (false) {
				BluetoothSocket client = adapter.getRemoteDevice(
						"78:1C:5A:D1:BD:72")
						.createInsecureRfcommSocketToServiceRecord(uuid);
				client.connect();
				new Peer(client);
			}
			ssocket = adapter.listenUsingInsecureRfcommWithServiceRecord(
					"BTServer", uuid);
			while (true) {
				final BluetoothSocket socket = ssocket.accept();
				new Peer(socket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ssocket != null) {
				try {
					ssocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void update(final String message) {
		this.handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG)
						.show();
				String text = textview.getText().toString();
				text = message + "\n" + text;
				textview.setText(text);
			}
		});
	}

}
