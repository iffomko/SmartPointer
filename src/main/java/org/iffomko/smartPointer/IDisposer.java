package org.iffomko.smartPointer;

/**
 * Интерфейс, который декларирует возможность "уничтожать" ресурсы определенного типа
 * @param <Type> тип ресурса, который нужно "уничтожить"
 */
public interface IDisposer<Type> {
    /**
     * Закрывает ресурс
     * @param target ресурс, который нужно уничтожить
     */
    void dispose(Type target);
}
