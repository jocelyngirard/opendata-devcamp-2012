package net.team10.android.fragments;

import java.util.ArrayList;
import java.util.List;

import net.team10.android.TitleBar;
import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.smartnsoft.droid4me.framework.SmartAdapters;
import com.smartnsoft.droid4me.framework.SmartAdapters.BusinessViewWrapper;
import com.smartnsoft.droid4me.framework.SmartAdapters.SimpleBusinessViewWrapper;

public class PoiTypeChooserFragment
    extends SmartListViewFragmentV4<TitleBar.TitleBarAggregate, ListView>
{

  public class StringViewAttribute
  {

    private final TextView text;

    public StringViewAttribute(View view)
    {
      text = (TextView) view.findViewById(android.R.id.text1);
    }

    public void update(String arg3)
    {
      text.setText(arg3);
    }

  }

  public class StringViewWrapper
      extends SimpleBusinessViewWrapper<String>
  {

    public StringViewWrapper(String string)
    {
      super(string, 0, android.R.layout.simple_list_item_1);
    }

    @Override
    protected Object extractNewViewAttributes(Activity arg0, View arg1, String arg2)
    {
      return new StringViewAttribute(arg1);
    }

    @Override
    protected void updateView(Activity arg0, Object arg1, View arg2, String arg3, int arg4)
    {
      ((StringViewAttribute) arg1).update(arg3);
    }

  }

  public List<? extends BusinessViewWrapper<?>> retrieveBusinessObjectsList()
      throws BusinessObjectUnavailableException
  {
    final List<BusinessViewWrapper<?>> wrappers = new ArrayList<SmartAdapters.BusinessViewWrapper<?>>();
    // TODO Auto-generated method stub

    wrappers.add(new StringViewWrapper("toto"));
    wrappers.add(new StringViewWrapper("tata"));
    wrappers.add(new StringViewWrapper("titi"));
    wrappers.add(new StringViewWrapper("yooo"));

    return wrappers;
  }

}
