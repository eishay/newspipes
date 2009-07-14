// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SearchKeyword.scala

package com.newspipes.server.core;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.rmi.RemoteException;
import scala.ScalaObject;

public class SearchKeyword
    implements IsSerializable, ScalaObject
{

    public SearchKeyword(String key, String value, int count)
    {
        this.value = value;
        this.count = count;
        super();
    }

    public SearchKeyword()
    {
        Object _tmp = null;
        Object _tmp1 = null;
        this(null, null, 0);
    }

    public void count_$eq(int x$1)
    {
        count = x$1;
    }

    public int count()
    {
        return count;
    }

    public String value()
    {
        return value;
    }

    public int $tag()
        throws RemoteException
    {
        return scala.ScalaObject.class.$tag(this);
    }

    private int count;
    private final String value;
}
