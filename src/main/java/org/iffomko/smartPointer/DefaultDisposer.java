package org.iffomko.smartPointer;

/**
 * Обычный "уничтожитель" ресурсов, которые наследуются от <type>Autocloseable</type>
 */
class DefaultDisposer implements IDisposer<AutoCloseable>  {
    public final static DefaultDisposer INSTANCE = new DefaultDisposer();

    /**
     * Закрывает ресурс
     * @param target ресурс, который нужно уничтожить
     */
    @Override
    public void dispose(AutoCloseable target) {
        try {
            target.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
