package com.faizalsidek.osmdroid.custom;

import android.app.Application;
import android.content.Context;

import org.osmdroid.tileprovider.IMapTileProviderCallback;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.INetworkAvailablityCheck;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileFilesystemProvider;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.modules.TileWriter;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by 608761587 on 21/06/2015.
 */
public class MyTileProvider extends MapTileProviderArray implements
        IMapTileProviderCallback {

    public MyTileProvider(final Context pContext, final ITileSource pTileSource) {
        this(new SimpleRegisterReceiver(pContext), new NetworkAvailabliltyCheck(pContext), pTileSource);
    }

    protected MyTileProvider(final IRegisterReceiver pRegisterReceiver,
                             final INetworkAvailablityCheck aNetworkAvailablityCheck,
                             final ITileSource pTileSource) {
        super(pTileSource, pRegisterReceiver);

        // Look for raw tiles on the file system
        final MapTileFilesystemProvider fileSystemProvider = new MapTileFilesystemProvider(
                pRegisterReceiver, pTileSource);
        mTileProviderList.add(fileSystemProvider);

        // Look for tile archives on the file system
        final MapTileFileArchiveProvider archiveProvider = new MapTileFileArchiveProvider(
                pRegisterReceiver, pTileSource);
        mTileProviderList.add(archiveProvider);

        // Look for raw tiles on the Internet
        final TileWriter tileWriter = new TileWriter();
        final MyTileDownloader downloaderProvider = new MyTileDownloader(pTileSource, tileWriter, aNetworkAvailablityCheck);
        mTileProviderList.add(downloaderProvider);
    }
}
