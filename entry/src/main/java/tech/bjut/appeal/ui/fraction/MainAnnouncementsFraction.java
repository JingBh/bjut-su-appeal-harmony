package tech.bjut.appeal.ui.fraction;

import ohos.aafwk.ability.fraction.Fraction;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.components.element.VectorElement;
import ohos.media.image.ImageSource;
import okhttp3.Call;
import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.data.model.Announcement;
import tech.bjut.appeal.data.model.Attachment;
import tech.bjut.appeal.data.service.WebService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MainAnnouncementsFraction extends Fraction {

    private List<ItemData> items;

    private ListContainer listContainer;

    private String currentCursor = null;

    private boolean loading = false;

    private boolean loadFailed = false;

    private boolean loadFinished = false;

    private Call request = null;

    @Override
    protected Component onComponentAttached(LayoutScatter scatter, ComponentContainer container, Intent intent) {
        Component component = scatter.parse(ResourceTable.Layout_slice_announcements, container, false);

        items = new ArrayList<>();
        currentCursor = null;
        loadFinished = false;
        listContainer = component.findComponentById(ResourceTable.Id_announcements_list);
        listContainer.setItemProvider(new ItemProvider());
        listContainer.setScrollListener(() -> {
            if (loading || loadFinished) {
                return;
            }
            if (listContainer.getItemPosByVisibleIndex(listContainer.getVisibleIndexCount() - 1) >= listContainer.getItemProvider().getCount() - 3) {
                loadData();
            }
        });

        loadData();

        return component;
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (request != null) {
            request.cancel();
        }
    }

    private void loadData() {
        loading = true;
        listContainer.getItemProvider().notifyDataSetItemChanged(items.size());
        this.getMainTaskDispatcher().asyncDispatch(() -> {
            if (request != null) {
                request.cancel();
            }
            request = WebService.getAnnouncements(currentCursor, data -> {
                loading = false;
                if (this.getUITaskDispatcher() == null) {
                    return;
                }
                this.getUITaskDispatcher().syncDispatch(() -> {
                    if (this.getContext() == null) {
                        return;
                    }
                    if (data == null) {
                        loadFailed = true;
                    } else {
                        if (data.getPinned() != null && data.getPinned().size() > 0) {
                            items.clear();
                            items.addAll(data.getPinned().stream().map(ItemData::new).collect(Collectors.toList()));
                        }
                        if (data.getData() == null || data.getData().size() == 0) {
                            loadFinished = true;
                        } else {
                            items.addAll(data.getData().stream().map(ItemData::new).collect(Collectors.toList()));
                        }
                        currentCursor = data.getCursor();
                        listContainer.getItemProvider().notifyDataChanged();
                    }
                    listContainer.getItemProvider().notifyDataSetItemChanged(items.size());
                });
            });
        });
    }

    private static class ItemData {

        private final Announcement announcement;

        private final boolean shouldCollapse;

        private boolean collapsed;

        public ItemData(Announcement announcement) {
            this.announcement = announcement;
            this.shouldCollapse = announcement.getContent().length() > 150;
            this.collapsed = this.shouldCollapse;
        }

        public Announcement getAnnouncement() {
            return announcement;
        }

        public boolean shouldCollapse() {
            return shouldCollapse;
        }

        public boolean isCollapsed() {
            return collapsed;
        }

        public void setCollapsed(boolean collapsed) {
            this.collapsed = collapsed;
        }
    }

    private class ItemProvider extends BaseItemProvider {
        @Override
        public int getCount() {
            return items.size() + 1;
        }

        @Override
        public Object getItem(int i) {
            if (i == items.size()) {
                return null;
            }
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            if (i == items.size()) {
                return -1;
            }
            return items.get(i).getAnnouncement().getId();
        }

        @Override
        public Component getComponent(int i, Component convertComponent, ComponentContainer componentContainer) {
            if (getItemId(i) == -1) {
                final Text component;
                if (convertComponent != null && convertComponent.getId() == ResourceTable.Id_questions_filter) {
                    component = (Text) convertComponent;
                } else {
                    component = (Text) LayoutScatter.getInstance(getApplicationContext())
                            .parse(ResourceTable.Layout_component_list_hint, componentContainer, false);
                }
                if (loading) {
                    component.setText(ResourceTable.String_list_loading);
                } else if (loadFinished) {
                    component.setText(ResourceTable.String_list_no_more);
                } else if (loadFailed) {
                    component.setText(ResourceTable.String_list_load_failed);
                } else {
                    component.setVisibility(Component.HIDE);
                }
                return component;
            }

            ItemData item = items.get(i);
            Announcement announcement = item.getAnnouncement();
            final Component component;
            if (convertComponent != null && convertComponent.getId() == ResourceTable.Id_announcement_item) {
                component = convertComponent;
            } else {
                component = LayoutScatter.getInstance(getApplicationContext())
                    .parse(ResourceTable.Layout_component_announcement_item, componentContainer, false);
            }

            Text title = component.findComponentById(ResourceTable.Id_announcement_item_title);
            title.setText(announcement.getTitle());

            Image pinned = component.findComponentById(ResourceTable.Id_announcement_item_pinned);
            pinned.setVisibility(announcement.isPinned() ? Component.VISIBLE : Component.HIDE);

            Text date = component.findComponentById(ResourceTable.Id_announcement_item_date);
            ZonedDateTime updatedAt = ZonedDateTime.parse(announcement.getCreatedAt());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日", Locale.SIMPLIFIED_CHINESE);
            date.setText(updatedAt.withZoneSameInstant(ZoneId.systemDefault()).format(formatter));

            Text content = component.findComponentById(ResourceTable.Id_announcement_item_content);
            ComponentContainer collapse = component.findComponentById(ResourceTable.Id_announcement_item_collapse);
            Image collapseIcon = (Image) collapse.getComponentAt(0);
            Text collapseText = (Text) collapse.getComponentAt(1);
            if (item.shouldCollapse()) {
                if (item.isCollapsed()) {
                    content.setText(announcement.getContent().substring(0, 150) + "...");
                    collapseIcon.setImageElement(new VectorElement(getApplicationContext(), ResourceTable.Graphic_bi_caret_down_fill_brand));
                    collapseText.setText(ResourceTable.String_list_expand);
                } else {
                    content.setText(announcement.getContent());
                    collapseIcon.setImageElement(new VectorElement(getApplicationContext(), ResourceTable.Graphic_bi_caret_up_fill_brand));
                    collapseText.setText(ResourceTable.String_list_collapse);
                }
                collapse.setClickedListener(comp -> {
                    item.setCollapsed(!item.isCollapsed());
                    listContainer.getItemProvider().notifyDataSetItemChanged(i);
                });
                collapse.setVisibility(Component.VISIBLE);
            } else {
                content.setText(announcement.getContent());
                collapse.setVisibility(Component.HIDE);
            }

            Image image = component.findComponentById(ResourceTable.Id_announcement_item_image);
            image.setCornerRadius(16);
            List<Attachment> imageAttachments = announcement.getImageAttachments();
            if (imageAttachments.size() > 0) {
                getMainTaskDispatcher().asyncDispatch(() -> {
                    WebService.getAttachment(imageAttachments.get(0).getId(), stream -> {
                        if (stream != null) {
                            ImageSource imageSource = ImageSource.create(stream, new ImageSource.SourceOptions());
                            if (getUITaskDispatcher() != null) {
                                getUITaskDispatcher().syncDispatch(() -> {
                                    image.setPixelMap(imageSource.createPixelmap(new ImageSource.DecodingOptions()));
                                });
                            }
                        }
                    });
                });
                image.setVisibility(Component.VISIBLE);
            } else {
                image.setVisibility(Component.HIDE);
            }

            return component;
        }
    }
}
