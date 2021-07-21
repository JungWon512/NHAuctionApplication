package com.nh.auctionserver.socketio;

import java.util.Collection;
import java.util.UUID;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOChannelInitializer;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.listener.MultiTypeEventListener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketIOServer extends com.corundumstudio.socketio.SocketIOServer {
	private SocketIOHandler mSocketIOHandler;

	public SocketIOServer(Configuration configuration) {
		super(configuration);
		// TODO Auto-generated constructor stub
	}

	public SocketIOServer(Configuration configuration, SocketIOHandler socketIOHandler) {
		super(configuration);
		mSocketIOHandler = socketIOHandler;
	}

	public SocketIOHandler getSocketIOHandler() {
		return this.mSocketIOHandler;
	}

	@Override
	public void setPipelineFactory(SocketIOChannelInitializer pipelineFactory) {
		super.setPipelineFactory(pipelineFactory);
	}

	@Override
	public Collection<SocketIOClient> getAllClients() {
		return super.getAllClients();
	}

	@Override
	public SocketIOClient getClient(UUID uuid) {
		return super.getClient(uuid);
	}

	@Override
	public Collection<SocketIONamespace> getAllNamespaces() {
		return super.getAllNamespaces();
	}

	@Override
	public BroadcastOperations getBroadcastOperations() {
		return super.getBroadcastOperations();
	}

	@Override
	public BroadcastOperations getRoomOperations(String room) {
		return super.getRoomOperations(room);
	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public Future<Void> startAsync() {
		return super.startAsync();
	}

	@Override
	protected void applyConnectionOptions(ServerBootstrap bootstrap) {
		super.applyConnectionOptions(bootstrap);
	}

	@Override
	protected void initGroups() {
		super.initGroups();
	}

	@Override
	public void stop() {
		super.stop();
	}

	@Override
	public SocketIONamespace addNamespace(String name) {
		return super.addNamespace(name);
	}

	@Override
	public SocketIONamespace getNamespace(String name) {
		return super.getNamespace(name);
	}

	@Override
	public void removeNamespace(String name) {
		super.removeNamespace(name);
	}

	@Override
	public Configuration getConfiguration() {
		return super.getConfiguration();
	}

	@Override
	public void addMultiTypeEventListener(String eventName, MultiTypeEventListener listener, Class<?>... eventClass) {
		super.addMultiTypeEventListener(eventName, listener, eventClass);
	}

	@Override
	public <T> void addEventListener(String eventName, Class<T> eventClass, DataListener<T> listener) {
		super.addEventListener(eventName, eventClass, listener);
	}

	@Override
	public void addDisconnectListener(DisconnectListener listener) {
		super.addDisconnectListener(listener);
	}

	@Override
	public void addConnectListener(ConnectListener listener) {
		super.addConnectListener(listener);
	}

	@Override
	public void addListeners(Object listeners) {
		super.addListeners(listeners);
	}

	@Override
	public void addListeners(Object listeners, Class listenersClass) {
		super.addListeners(listeners, listenersClass);
	}

}
