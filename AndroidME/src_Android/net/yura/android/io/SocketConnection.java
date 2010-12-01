/*
 *  MicroEmulator
 *  Copyright (C) 2001-2003 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */

package net.yura.android.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.Vector;

import net.yura.android.AndroidMeActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class SocketConnection implements javax.microedition.io.SocketConnection {


    private static ConnectivityBroadcastReceiver socketBroadcastReceiver;

    protected Socket socket;

	public SocketConnection() {
	    //#debug debug
	    System.out.println(">>> SocketConnection: Constructor()");

	    ConnectivityBroadcastReceiver.addSocketConnection(this);
	}

	public SocketConnection(String host, int port) throws IOException {
	    this();
		this.socket = new Socket(host, port);
	}

	public SocketConnection(Socket socket) {
	    this();
		this.socket = socket;
	}

	public String getAddress() throws IOException {
		if (socket == null || socket.isClosed()) {
			throw new IOException();
		}

		return socket.getInetAddress().toString();
	}

	public String getLocalAddress() throws IOException {
		if (socket == null || socket.isClosed()) {
			throw new IOException();
		}

		return socket.getLocalAddress().toString();
	}

	public int getLocalPort() throws IOException {
		if (socket == null || socket.isClosed()) {
			throw new IOException();
		}

		return socket.getLocalPort();
	}

	public int getPort() throws IOException {
		if (socket == null || socket.isClosed()) {
			throw new IOException();
		}

		return socket.getPort();
	}

	public int getSocketOption(byte option) throws IllegalArgumentException,
			IOException {
		if (socket != null && socket.isClosed()) {
			throw new IOException();
		}
		switch (option) {
    		case DELAY:
    		    return (socket.getTcpNoDelay()) ? 1 : 0;
    		case LINGER:
    			int value = socket.getSoLinger();
    			return (value == -1) ? 0 : value;
    		case KEEPALIVE:
    		    return (socket.getKeepAlive()) ? 1 : 0;
    		case RCVBUF:
    			return socket.getReceiveBufferSize();
    		case SNDBUF:
    			return socket.getSendBufferSize();
    		default:
    			throw new IllegalArgumentException();
		}
	}

	public void setSocketOption(byte option, int value)
			throws IllegalArgumentException, IOException {
		if (socket.isClosed()) {
			throw new IOException();
		}
		switch (option) {
		case DELAY:
			socket.setTcpNoDelay(value != 0);
			break;
		case LINGER:
			if (value < 0) {
				throw new IllegalArgumentException();
			}
			socket.setSoLinger(value != 0, value);
			break;
		case KEEPALIVE:
			socket.setKeepAlive(value != 0);
			break;
		case RCVBUF:
			if (value <= 0) {
				throw new IllegalArgumentException();
			}
			socket.setReceiveBufferSize(value);
			break;
		case SNDBUF:
			if (value <= 0) {
				throw new IllegalArgumentException();
			}
			socket.setSendBufferSize(value);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	public void close() throws IOException {
		// TODO fix differences between Java ME and Java SE

	    //#debug debug
        System.out.println(">>> SocketConnection: close()");

	    try {
	        socket.shutdownInput();
        } catch (Throwable e) {}

        try {
            socket.shutdownOutput();
        } catch (Throwable e) {}

		socket.close();
	}

	public InputStream openInputStream() throws IOException {
		return socket.getInputStream();
	}

	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(openInputStream());
	}

	public OutputStream openOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(openOutputStream());
	}


	// ----------------- SocketBroadcastReceiver INNER CLASS --------------------
	/**
	 *  See Android Issues 6144, 7935, 7933, etc. The socket does not throw IOException
	 * when a thread is reading from it (and is blocked), and the connection is drop. It just
	 * hangs forever reading... Writing does not throw any exception either.
	 *  To work around this, we keep a list of all open socket connections (in weak refs)
	 *  and we listen for CONNECTIVITY broadcast events. When the connection is dropped,
	 *  changes status or there is a new active connection (e.g. moving from WIFI to
	 *  mobile), we close all socket connections.
	 */
	static class ConnectivityBroadcastReceiver extends BroadcastReceiver {

        static private synchronized void addSocketConnection(SocketConnection socket) {
            if (socketBroadcastReceiver == null) {
                socketBroadcastReceiver = new ConnectivityBroadcastReceiver();
            }

            socketBroadcastReceiver.cleanSocketConnections(false); // Clean weak refs
            socketBroadcastReceiver.socketWeakList.add(new WeakReference<SocketConnection>(socket));
        }

        private Vector<WeakReference<SocketConnection>> socketWeakList = new Vector<WeakReference<SocketConnection>>();
        private int networkType;
        private State networkState;

        private ConnectivityBroadcastReceiver() {
            updateConnectivity(false); // Initialize

            //#debug debug
            System.out.println(">>> SocketBroadcastReceiver: registerReceiver");

            // Listen for Network events
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            AndroidMeActivity.DEFAULT_ACTIVITY.registerReceiver(this, intentFilter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //#debug debug
            System.out.println(">>> SocketBroadcastReceiver: received " + intent.getAction() + ": " + intent.getExtras());
            updateConnectivity(true);
        }

        private void updateConnectivity(boolean closeConnections) {
            ConnectivityManager connectivityManager = (ConnectivityManager) AndroidMeActivity.DEFAULT_ACTIVITY.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

                if (netInfo == null) {
                    cleanSocketConnections(closeConnections);
                }
                else {
                    State state = netInfo.getState();
                    int type = netInfo.getType();

                    //#mdebug debug
                    System.out.println(">>> SocketBroadcastReceiver: state = " + state +
                            " detail = " + netInfo.getDetailedState() + " type = " + type +
                            " name = " + netInfo.getTypeName());
                    //#enddebug

                    if (type != networkType || state != networkState || !netInfo.isConnected()) {
                        networkType = type;
                        networkState = state;
                        cleanSocketConnections(closeConnections);
                    }
                }
            }
        }

        private void cleanSocketConnections(boolean closeConnections) {
            //#debug debug
            System.out.println(">>> SocketBroadcastReceiver: closeSocketConnections() " + socketWeakList.size());

            // NOTE: Looping from the end of the vector, so we can safely delete vector elements
            for (int i = socketWeakList.size() - 1; i >= 0; i--) {
                SocketConnection conn = socketWeakList.elementAt(i).get();
                if (closeConnections) {
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (Throwable e) {
                        }
                    }
                    conn = null;
                }

                if (conn == null) {
                    socketWeakList.removeElementAt(i);
                }
            }

            if (closeConnections) {
                synchronized (ConnectivityBroadcastReceiver.class) {
                    if (socketBroadcastReceiver != null && socketWeakList.size() == 0) {
                        //#debug debug
                        System.out.println(">>> SocketBroadcastReceiver: close");
                        AndroidMeActivity.DEFAULT_ACTIVITY.unregisterReceiver(socketBroadcastReceiver);
                        socketBroadcastReceiver = null;
                    }
                }
            }
        }
    }
}
