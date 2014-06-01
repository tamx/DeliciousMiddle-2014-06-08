package com.example.btchat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
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
			final View rootView = inflater.inflate(R.layout.fragment_main,
					container, false);
			Button button = (Button) rootView.findViewById(R.id.button1);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					EditText edit = (EditText) rootView
							.findViewById(R.id.editText1);
					String message = edit.getText().toString();
					Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT)
							.show();
					try {
						// Unique UUID for this application
						UUID uuid = UUID
								.fromString("00000000-1111-1111-0000-111111111111");
						BluetoothAdapter adapter = BluetoothAdapter
								.getDefaultAdapter();
						BluetoothDevice device = adapter
								.getRemoteDevice("78:1C:5A:D1:BD:72");
						BluetoothSocket socket = device
								.createInsecureRfcommSocketToServiceRecord(uuid);
						socket.connect();
						OutputStream os = socket.getOutputStream();
						os.write((message + "\n").getBytes());
						os.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			return rootView;
		}
	}

}
