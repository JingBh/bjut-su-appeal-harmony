package tech.bjut.appeal.ui.fraction;

import ohos.aafwk.ability.fraction.Fraction;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.window.dialog.CommonDialog;
import ohos.media.image.ImageSource;
import okhttp3.Call;
import tech.bjut.appeal.ResourceTable;
import tech.bjut.appeal.data.model.Answer;
import tech.bjut.appeal.data.model.Attachment;
import tech.bjut.appeal.data.model.CampusEnum;
import tech.bjut.appeal.data.model.Question;
import tech.bjut.appeal.data.service.WebService;
import tech.bjut.appeal.ui.util.DialogUtil;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class QuestionsFraction extends Fraction {

    private boolean filter;

    private boolean history;

    private List<Question> items;

    private List<Question> filteredItems;

    private CampusEnum filterCampus;

    private String searchQuery;

    private ListContainer listContainer;

    private String currentCursor = null;

    private boolean loading = false;

    private boolean loadFailed = false;

    private boolean loadFinished = false;

    private Call request = null;

    public QuestionsFraction(boolean filter, boolean history) {
        super();
        this.filter = filter;
        this.history = history;
    }

    @Override
    protected Component onComponentAttached(LayoutScatter scatter, ComponentContainer container, Intent intent) {
        Component component = scatter.parse(ResourceTable.Layout_slice_questions, container, false);

        filterCampus = null;
        searchQuery = "";

        items = new ArrayList<>();
        filteredItems = new ArrayList<>();
        currentCursor = null;
        loadFinished = false;
        listContainer = component.findComponentById(ResourceTable.Id_answers_list);
        listContainer.setItemProvider(new ItemProvider());
        listContainer.setScrollListener(this::onScrollBottom);

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

    private void onScrollBottom() {
        if (loading || loadFinished) {
            return;
        }
        if (listContainer.getItemPosByVisibleIndex(listContainer.getVisibleIndexCount() - 1) >= listContainer.getItemProvider().getCount() - 3) {
            loadData();
        }
    }

    private void loadData() {
        loading = true;
        listContainer.getItemProvider().notifyDataSetItemChanged(filteredItems.size() + (filter ? 1 : 0));
        this.getMainTaskDispatcher().asyncDispatch(() -> {
            if (request != null) {
                request.cancel();
            }
            request = WebService.getQuestions(currentCursor, history, data -> {
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
                        if (data.getData() == null || data.getData().size() == 0) {
                            loadFinished = true;
                        } else {
                            items.addAll(data.getData());
                            filteredItems = getFilteredItems();
                        }
                        currentCursor = data.getCursor();
                        listContainer.getItemProvider().notifyDataChanged();
                    }
                    listContainer.getItemProvider().notifyDataSetItemChanged(filteredItems.size() + (filter ? 1 : 0));
                });
            });
        });
    }

    public List<Question> getFilteredItems() {
        if (items == null) {
            return new ArrayList<>();
        }

        if (!filter) {
            return items;
        }

        return items.stream().filter(question -> {
            if (filterCampus != null && question.getCampus() != filterCampus) {
                return false;
            }
            if (searchQuery != null
                && !searchQuery.isEmpty()
                && !question.getContent().contains(searchQuery)
                && !(question.getAnswer() != null && question.getAnswer().getContent().contains(searchQuery))) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
        listContainer.getItemProvider().notifyDataChanged();
        onScrollBottom();
    }

    public void setHistory(boolean history) {
        this.history = history;
        this.items.clear();
        this.currentCursor = null;
        this.loadFinished = false;
        this.loadData();
    }

    private class ItemProvider extends BaseItemProvider {
        @Override
        public int getCount() {
            return filteredItems.size() + (filter ? 2 : 1);
        }

        @Override
        public Object getItem(int i) {
            if (filter) {
                if (i == 0 || i == filteredItems.size() + 1) {
                    return null;
                }
                return filteredItems.get(i - 1);
            } else {
                if (i == filteredItems.size()) {
                    return null;
                }
                return filteredItems.get(i);
            }
        }

        @Override
        public long getItemId(int i) {
            if (filter) {
                if (i == 0) {
                    return -2;
                }
                if (i == filteredItems.size() + 1) {
                    return -1;
                }
                return filteredItems.get(i - 1).getId();
            } else {
                if (i == filteredItems.size()) {
                    return -1;
                }
                return filteredItems.get(i).getId();
            }
        }

        @Override
        public Component getComponent(int i, Component convertComponent, ComponentContainer componentContainer) {
            if (getItemId(i) == -2) {
                final Component component;
                if (convertComponent != null && convertComponent.getId() == ResourceTable.Id_questions_filter) {
                    component = convertComponent;
                } else {
                    component = LayoutScatter.getInstance(getApplicationContext())
                        .parse(ResourceTable.Layout_component_question_filter, componentContainer, false);

                    component.findComponentById(ResourceTable.Id_questions_filter_campus)
                        .setClickedListener(listener -> {
                            DialogUtil.showCampusChoose(QuestionsFraction.this, filterCampus, campus -> {
                                filterCampus = campus;
                                filteredItems = getFilteredItems();
                                listContainer.getItemProvider().notifyDataChanged();
                                onScrollBottom();
                            });
                        });

                    ((TextField) component.findComponentById(ResourceTable.Id_questions_filter_search))
                        .addTextObserver((text, start, end, count) -> {
                            searchQuery = text;
                            filteredItems = getFilteredItems();
                            listContainer.getItemProvider().notifyDataChanged();
                            onScrollBottom();
                        });
                }

                ((TextField) component.findComponentById(ResourceTable.Id_questions_filter_search))
                    .setText(searchQuery);

                return component;
            }

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

            Question question = (Question) Objects.requireNonNull(getItem(i));
            final Component component;
            if (convertComponent != null && convertComponent.getId() == ResourceTable.Id_question_item) {
                component = convertComponent;
            } else {
                component = LayoutScatter.getInstance(QuestionsFraction.this)
                    .parse(ResourceTable.Layout_component_question_item, componentContainer, false);
            }

            Text date = component.findComponentById(ResourceTable.Id_question_item_question_time);
            ZonedDateTime updatedAt = ZonedDateTime.parse(question.getCreatedAt());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm", Locale.SIMPLIFIED_CHINESE);
            date.setText(updatedAt.withZoneSameInstant(ZoneId.systemDefault()).format(formatter));

            Text content = component.findComponentById(ResourceTable.Id_question_item_question_content);
            content.setText(question.getContent());

            Image image = component.findComponentById(ResourceTable.Id_question_item_question_image);
            image.setCornerRadius(16);
            List<Attachment> imageAttachments = question.getImageAttachments();
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

            Text campus = component.findComponentById(ResourceTable.Id_question_item_question_campus);
            if (question.getCampus() != null) {
                campus.setText(QuestionsFraction.this.getString(ResourceTable.String_questions_campus)
                    + QuestionsFraction.this.getString(CampusEnum.toString(question.getCampus())));
                campus.setVisibility(Component.VISIBLE);
            } else {
                campus.setVisibility(Component.HIDE);
            }

            Component answerComponent = component.findComponentById(ResourceTable.Id_question_item_answer);
            Component answerComponentBorder = component.findComponentById(ResourceTable.Id_question_item_answer_border);
            if (question.getAnswer() != null) {
                Answer answer = question.getAnswer();
                answerComponent.setVisibility(Component.VISIBLE);
                answerComponentBorder.setVisibility(Component.VISIBLE);

                Text answerDate = component.findComponentById(ResourceTable.Id_question_item_answer_time);
                ZonedDateTime answerUpdatedAt = ZonedDateTime.parse(answer.getCreatedAt());
                DateTimeFormatter answerFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm", Locale.SIMPLIFIED_CHINESE);
                answerDate.setText(answerUpdatedAt.withZoneSameInstant(ZoneId.systemDefault()).format(answerFormatter));

                Text answerContent = component.findComponentById(ResourceTable.Id_question_item_answer_content);
                answerContent.setText(answer.getContent());

                Image answerImage = component.findComponentById(ResourceTable.Id_question_item_answer_image);
                answerImage.setCornerRadius(16);
                List<Attachment> answerImageAttachments = answer.getImageAttachments();
                if (answerImageAttachments.size() > 0) {
                    getMainTaskDispatcher().asyncDispatch(() -> {
                        WebService.getAttachment(answerImageAttachments.get(0).getId(), stream -> {
                            if (stream != null) {
                                ImageSource imageSource = ImageSource.create(stream, new ImageSource.SourceOptions());
                                if (getUITaskDispatcher() != null) {
                                    getUITaskDispatcher().syncDispatch(() -> {
                                        answerImage.setPixelMap(imageSource.createPixelmap(new ImageSource.DecodingOptions()));
                                    });
                                }
                            }
                        });
                    });
                    answerImage.setVisibility(Component.VISIBLE);
                } else {
                    answerImage.setVisibility(Component.HIDE);
                }
            } else {
                answerComponent.setVisibility(Component.HIDE);
                answerComponentBorder.setVisibility(Component.HIDE);
            }

            Component actionsComponent = component.findComponentById(ResourceTable.Id_question_item_actions);
            Component actionsComponentBorder = component.findComponentById(ResourceTable.Id_question_item_actions_border);
            if (question.getAnswer() == null) {
                actionsComponent.setVisibility(Component.VISIBLE);
                actionsComponentBorder.setVisibility(Component.VISIBLE);

                actionsComponent.findComponentById(ResourceTable.Id_question_item_actions_delete)
                    .setClickedListener(listener -> {
                        DialogUtil.showConfirm(QuestionsFraction.this, ResourceTable.String_questions_delete_confirm, true, () -> {
                            CommonDialog dialog = DialogUtil.showLoading(QuestionsFraction.this);
                            QuestionsFraction.this.getMainTaskDispatcher().asyncDispatch(() -> {
                                WebService.deleteQuestion(question.getId(), success -> {
                                    if (QuestionsFraction.this.getUITaskDispatcher() == null) {
                                        return;
                                    }
                                    if (success) {
                                        QuestionsFraction.this.getUITaskDispatcher().syncDispatch(() -> {
                                            dialog.destroy();
                                            DialogUtil.showToast(QuestionsFraction.this, ResourceTable.String_questions_delete_success);
                                            items = new ArrayList<>();
                                            filteredItems = new ArrayList<>();
                                            currentCursor = null;
                                            loadFinished = false;
                                            loadData();
                                        });
                                    } else {
                                        QuestionsFraction.this.getUITaskDispatcher().syncDispatch(() -> {
                                            dialog.destroy();
                                            DialogUtil.showToast(QuestionsFraction.this, ResourceTable.String_questions_delete_failed);
                                        });
                                    }
                                });
                            });
                        });
                    });
            } else {
                actionsComponent.setVisibility(Component.HIDE);
                actionsComponentBorder.setVisibility(Component.HIDE);
            }

            return component;
        }
    }
}
