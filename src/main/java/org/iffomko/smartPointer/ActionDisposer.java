package org.iffomko.smartPointer;

import java.util.function.Consumer;

/**
 * "Уничтожитель" ресурсов, который может уничтожать с помощью входной функции.
 * Очень важно, чтобы входная функция действительно могла закрывать этот ресурс,
 * иначе могут возникнуть проблемы
 * @param <Type> тип уничтожаемого объекта
 */
class ActionDisposer<Type> implements IDisposer<Type> {
    private final Consumer<Type> action;

    /**
     * Конструктор, который принимает функцию, закрывающая объекты определенного типа
     * @param action функция, закрывающая объекты
     */
    ActionDisposer(Consumer<Type> action) {
        this.action = action;
    }

    /**
     * Закрывает ресурс
     * @param target ресурс, который нужно уничтожить
     */
    @Override
    public void dispose(Type target) {
        action.accept(target);
    }
}
