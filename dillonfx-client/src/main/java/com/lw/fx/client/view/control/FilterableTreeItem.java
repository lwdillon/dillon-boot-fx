package com.lw.fx.client.view.control;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import java.util.function.Predicate;

public class FilterableTreeItem<T> extends CheckBoxTreeItem<T> {
    private final ObservableList<TreeItem<T>> sourceList = FXCollections.observableArrayList();
    private final FilteredList<TreeItem<T>> filteredList = new FilteredList<>(this.sourceList);

    private final ObjectProperty<TreeItemPredicate<T>> predicate = new SimpleObjectProperty<>();

    /**
     * Creates a new {@link TreeItem} with sorted children. To enable sorting it is
     * necessary to set the {@link TreeItemComparator}. If no comparator is set, then
     * the tree item will attempt so bind itself to the comparator of its parent.
     *
     * @param value the value of the {@link TreeItem}
     */
    public FilterableTreeItem(T value) {
        super(value);
        this.filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> {
            Predicate<TreeItem<T>> p = child -> {
                // Set the predicate of child items to force filtering
                if (child instanceof FilterableTreeItem) {
                    FilterableTreeItem<T> filterableChild = (FilterableTreeItem<T>) child;
                    filterableChild.setPredicate(this.predicate.get());
                }
                child.setExpanded(true);
                // If there is no predicate, keep this tree item
                if (this.predicate.get() == null)
                    return true;
                // If there are children, keep this tree item
                if (child.getChildren().size() > 0)
                    return true;
                // Otherwise ask the TreeItemPredicate
                return this.predicate.get().test(this, child.getValue());
            };
            return p;
        }, this.predicate));

        Bindings.bindContent(getChildren(), getBackingList());
    }

    /**
     * @return the backing list
     */
    protected ObservableList<TreeItem<T>> getBackingList() {
        return this.filteredList;
    }

    /**
     * Returns the list of children that is backing the filtered list.
     *
     * @return underlying list of children
     */
    public ObservableList<TreeItem<T>> getInternalChildren() {
        return this.sourceList;
    }

    /**
     * @return the predicate property
     */
    public final ObjectProperty<TreeItemPredicate<T>> predicateProperty() {
        return this.predicate;
    }

    /**
     * @return the predicate
     */
    public final TreeItemPredicate<T> getPredicate() {
        return this.predicate.get();
    }

    /**
     * Set the predicate
     *
     * @param predicate the predicate
     */
    public final void setPredicate(TreeItemPredicate<T> predicate) {
        this.predicate.set(predicate);
    }
}