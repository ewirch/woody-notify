package edu.wirch.woody.bitbucket.conf;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class PresenterTest {

    @Test
    public void verifyGetReturnsFalseIfNotAdmin() throws IOException {
        final Presenter presenter = new Presenter(getUserManager_CurrentUserNonAdmin(), mock(TemplateRenderer.class), mock(PluginSettingsStore.class));

        assertThat(presenter.get(mock(PrintWriter.class))).isFalse();
    }

    @Test
    public void verifyGetReturnsTrueIfAdmin() throws IOException {
        final PluginSettingsStore settingsStore = mockSettingsStoreReturning(GlobalConfiguration.builder().build());

        final Presenter presenter = new Presenter(getUserManager_CurrentUserIsAdmin(), mock(TemplateRenderer.class), settingsStore);

        assertThat(presenter.get(mock(PrintWriter.class))).isTrue();
    }

    @Test
    public void verifyGetLoadsSettings() throws IOException {
        final GlobalConfiguration conf = mock(GlobalConfiguration.class);
        final PluginSettingsStore settingsStore = mockSettingsStoreReturning(conf);

        final Presenter presenter = new Presenter(getUserManager_CurrentUserIsAdmin(), mock(TemplateRenderer.class), settingsStore);
        presenter.get(mock(Writer.class));

        verify(settingsStore).load();
    }

    @Test
    public void verifyGetPopulatesContext() throws IOException {
        final TemplateRenderer templateRenderer = mock(TemplateRenderer.class);
        final GlobalConfiguration conf = mock(GlobalConfiguration.class);
        final PluginSettingsStore settingsStore = mockSettingsStoreReturning(conf);

        final Presenter presenter = new Presenter(getUserManager_CurrentUserIsAdmin(), templateRenderer, settingsStore);
        presenter.get(mock(PrintWriter.class));

        final ArgumentCaptor<Map> contextCaptor = ArgumentCaptor.forClass(Map.class);
        verify(conf).toMap(contextCaptor.capture());
        verify(templateRenderer).render(anyString(), same(contextCaptor.getValue()), any(Writer.class));
    }

    @Test
    public void verifySetLoadsSettings() throws IOException {
        final GlobalConfiguration.Builder confBuilder = mock(GlobalConfiguration.Builder.class);
        final PluginSettingsStore settingsStore = mockSettingsStoreReturning(confBuilder);
        when(confBuilder.build()).thenReturn(mock(GlobalConfiguration.class));

        final Presenter presenter = new Presenter(getUserManager_CurrentUserIsAdmin(), mock(TemplateRenderer.class), settingsStore);
        presenter.set(new HashMap<>(), mock(Writer.class));

        verify(settingsStore).loadBuilder();
    }

    @Test
    public void verifySetReadsParameters() throws IOException {
        final GlobalConfiguration.Builder builder = mock(GlobalConfiguration.Builder.class);
        final PluginSettingsStore settingsStore = mockSettingsStoreReturning(builder);
        when(builder.build()).thenReturn(mock(GlobalConfiguration.class));

        final Presenter presenter = new Presenter(getUserManager_CurrentUserIsAdmin(), mock(TemplateRenderer.class), settingsStore);
        final HashMap<String, String> params = new HashMap<>();
        presenter.set(params, mock(Writer.class));

        verify(builder).fromMap(same(params));
    }

    @Test
    public void verifySetSetsSaveWhenValid() throws IOException {
        final PluginSettingsStore settingsStore = mockSettingsStoreReturning(GlobalConfiguration.builder());
        final TemplateRenderer templateRenderer = mock(TemplateRenderer.class);

        final Presenter presenter = new Presenter(getUserManager_CurrentUserIsAdmin(), templateRenderer, settingsStore);
        presenter.set(map("webhook", "https://test"), mock(Writer.class));

        final ArgumentCaptor<Map> contextCaptor = ArgumentCaptor.forClass(Map.class);
        verify(templateRenderer).render(anyString(), contextCaptor.capture(), any(Writer.class));
        assertThat(contextCaptor.getValue()).includes(entry("saved", true));
    }

    @Test
    public void verifySetSetsErrorsWhenInvalid() throws IOException {
        final PluginSettingsStore settingsStore = mockSettingsStoreReturning(GlobalConfiguration.builder());
        final TemplateRenderer templateRenderer = mock(TemplateRenderer.class);

        final Presenter presenter = new Presenter(getUserManager_CurrentUserIsAdmin(), templateRenderer, settingsStore);
        presenter.set(map("webhook", ""), mock(Writer.class));

        final ArgumentCaptor<Map> contextCaptor = ArgumentCaptor.forClass(Map.class);
        verify(templateRenderer).render(anyString(), contextCaptor.capture(), any(Writer.class));
        assertThat(contextCaptor.getValue()
                                .containsKey("errors")).isTrue();
        assertThat(contextCaptor.getValue()
                                .get("errors")).isInstanceOf(Map.class);
        final Map errors = (Map) contextCaptor.getValue()
                                              .get("errors");
        assertThat(errors.size()).isEqualTo(1);
    }

    @Test
    public void verifySetPopulatesContextWhenValid() throws IOException {
        final PluginSettingsStore settingsStore = mockSettingsStoreReturning(GlobalConfiguration.builder());
        final TemplateRenderer templateRenderer = mock(TemplateRenderer.class);


        final Presenter presenter = new Presenter(getUserManager_CurrentUserIsAdmin(), templateRenderer, settingsStore);
        presenter.set(map("webhook", "https://test"), mock(Writer.class));

        final ArgumentCaptor<Map> contextCaptor = ArgumentCaptor.forClass(Map.class);
        verify(templateRenderer).render(anyString(), contextCaptor.capture(), any(Writer.class));
        assertThat(contextCaptor.getValue()).includes(entry("webhook", "https://test"));
    }

    @Test
    public void verifySetPopulatesContextWhenInvalid() throws IOException {
        final GlobalConfiguration.Builder builder = GlobalConfiguration.builder();
        final PluginSettingsStore settingsStore = mockSettingsStoreReturning(builder);
        final TemplateRenderer templateRenderer = mock(TemplateRenderer.class);


        final Presenter presenter = new Presenter(getUserManager_CurrentUserIsAdmin(), templateRenderer, settingsStore);
        presenter.set(map("webhook", ""), mock(Writer.class));

        final ArgumentCaptor<Map> contextCaptor = ArgumentCaptor.forClass(Map.class);
        verify(templateRenderer).render(anyString(), contextCaptor.capture(), any(Writer.class));
        assertThat(contextCaptor.getValue()).includes(entry("webhook", ""));
    }

    @Nonnull
    private PluginSettingsStore mockSettingsStoreReturning(final GlobalConfiguration.Builder builder) {
        final PluginSettingsStore settingsStore = mock(PluginSettingsStore.class);
        when(settingsStore.loadBuilder()).thenReturn(builder);
        return settingsStore;
    }

      @Nonnull
    private PluginSettingsStore mockSettingsStoreReturning(final GlobalConfiguration conf) {
        final PluginSettingsStore settingsStore = mock(PluginSettingsStore.class);
        when(settingsStore.load()).thenReturn(conf);
        return settingsStore;
    }

    private Map<String, String> map(final String key, final String value) {
        final Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return map;
    }


    private UserManager getUserManager_CurrentUserNonAdmin() {
        final UserManager userManager = mock(UserManager.class);
        final UserKey userKey = new UserKey("");
        when(userManager.getRemoteUserKey()).thenReturn(userKey);
        when(userManager.isSystemAdmin(same(userKey))).thenReturn(false);
        return userManager;
    }

    private UserManager getUserManager_CurrentUserIsAdmin() {
        final UserManager userManager = mock(UserManager.class);
        final UserKey userKey = new UserKey("");
        when(userManager.getRemoteUserKey()).thenReturn(userKey);
        when(userManager.isSystemAdmin(same(userKey))).thenReturn(true);
        return userManager;
    }

}