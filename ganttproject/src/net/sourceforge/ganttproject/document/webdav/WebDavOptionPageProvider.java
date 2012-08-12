package net.sourceforge.ganttproject.document.webdav;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;

import net.sourceforge.ganttproject.gui.AbstractTableAndActionsComponent.SelectionListener;
import net.sourceforge.ganttproject.gui.EditableList;
import net.sourceforge.ganttproject.gui.options.OptionPageProviderBase;
import net.sourceforge.ganttproject.gui.options.OptionsPageBuilder;
import net.sourceforge.ganttproject.gui.options.model.ChangeValueEvent;
import net.sourceforge.ganttproject.gui.options.model.ChangeValueListener;
import net.sourceforge.ganttproject.gui.options.model.DefaultStringOption;
import net.sourceforge.ganttproject.gui.options.model.GPOptionGroup;
import net.sourceforge.ganttproject.gui.options.model.ListOption;

import com.google.common.collect.Lists;

public class WebDavOptionPageProvider extends OptionPageProviderBase {


  public WebDavOptionPageProvider() {
    super("storage.webdav");
    // TODO Auto-generated constructor stub
  }

  @Override
  public GPOptionGroup[] getOptionGroups() {
    // TODO Auto-generated method stub
    return new GPOptionGroup[0];
  }

  @Override
  public boolean hasCustomComponent() {
    return true;
  }

  @Override
  public Component buildPageComponent() {
    WebDavStorageImpl webdavStorage = (WebDavStorageImpl) getProject().getDocumentManager().getWebDavStorageUi();
    final ListOption<WebDavServerDescriptor> serversOption = webdavStorage.getServersOption();
    final EditableList<WebDavServerDescriptor> serverList = new EditableList<WebDavServerDescriptor>(
        Lists.newArrayList(serversOption.getValues()), Collections.EMPTY_LIST) {

          @Override
          protected WebDavServerDescriptor updateValue(WebDavServerDescriptor newValue, WebDavServerDescriptor curValue) {
            curValue.name = newValue.name;
            return curValue;
          }

          @Override
          protected WebDavServerDescriptor createValue(WebDavServerDescriptor prototype) {
            serversOption.addValue(prototype);
            return prototype;
          }

          @Override
          protected void deleteValue(WebDavServerDescriptor value) {
            serversOption.removeValueIndex(findIndex(value));
          }

          private int findIndex(WebDavServerDescriptor value) {
            return Lists.newArrayList(serversOption.getValues()).indexOf(value);
          }

          @Override
          protected WebDavServerDescriptor createPrototype(Object editValue) {
            return new WebDavServerDescriptor(String.valueOf(editValue), "", "");
          }

          @Override
          protected String getStringValue(WebDavServerDescriptor t) {
            return t.name;
          }
    };
    serverList.setUndefinedValueLabel("<type server name here>");

    final DefaultStringOption urlOption = new DefaultStringOption("webdav.server.url");
    urlOption.addChangeValueListener(new ChangeValueListener() {
      @Override
      public void changeValue(ChangeValueEvent event) {
        serverList.getSelectedObject().rootUrl = urlOption.getValue();
      }
    });

    final DefaultStringOption usernameOption = new DefaultStringOption("webdav.server.username");
    usernameOption.addChangeValueListener(new ChangeValueListener() {
      @Override
      public void changeValue(ChangeValueEvent event) {
        serverList.getSelectedObject().username = usernameOption.getValue();
      }
    });

    final DefaultStringOption passwordOption = new DefaultStringOption("webdav.server.password");
    passwordOption.addChangeValueListener(new ChangeValueListener() {
      @Override
      public void changeValue(ChangeValueEvent event) {
        serverList.getSelectedObject().password = passwordOption.getValue();
      }
    });
    passwordOption.setScreened(true);

    GPOptionGroup optionGroup = new GPOptionGroup("webdav", urlOption, usernameOption, passwordOption);

    OptionsPageBuilder builder = new OptionsPageBuilder(null, OptionsPageBuilder.ONE_COLUMN_LAYOUT);
    serverList.getTableAndActions().addSelectionListener(new SelectionListener<WebDavServerDescriptor>() {
      @Override
      public void selectionChanged(List<WebDavServerDescriptor> selection) {
        if (selection.size() == 1) {
          WebDavServerDescriptor selected = selection.get(0);
          urlOption.setValue(selected.rootUrl);
          usernameOption.setValue(selected.username);
          passwordOption.setValue(selected.password);
        }
      }
    });

    Box result = Box.createHorizontalBox();
    JPanel serversPanel = new JPanel(new BorderLayout());
    serversPanel.add(serverList.createDefaultComponent(), BorderLayout.CENTER);

    GPOptionGroup lockingGroup = new GPOptionGroup("webdav.lock", webdavStorage.getWebDavLockTimeoutOption(), webdavStorage.getWebDavReleaseLockOption());
    serversPanel.add(builder.buildPlanePage(new GPOptionGroup[] {lockingGroup}), BorderLayout.SOUTH);

    result.add(serversPanel);
    result.add(builder.buildPlanePage(new GPOptionGroup[] {optionGroup}));
    return OptionPageProviderBase.wrapContentComponent(result, "WebDAV servers", "foo");
  }
}