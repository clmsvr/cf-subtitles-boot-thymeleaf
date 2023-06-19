package cms.cf.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Classe que implementa um Cache generico com a politica de Last Recent Used
 * para descarte de objetos. Baseia-se nos metodos da classe
 * java.util.LinkedHashMap
 */
public class GenericLRUCache
{
    private InternalCache cache = null;

    /**
     * Construtor Default Inicializa o cache LRU com no maximo 16 entradas.
     */
    public GenericLRUCache()
    {
        cache = new InternalCache(16);
    }

    /**
     * Construtor Default
     * 
     * @param maxEntries
     *            numero de posicoes no cache.
     */
    public GenericLRUCache(int maxEntries)
    {
        cache = new InternalCache(maxEntries);
    }

    /**
     * Seta o numero maximo de entradas no cache.
     * 
     * @param maxEntries
     *            numero de entradas a ser setado.
     */
    public synchronized void setMaxEntries(int maxEntries)
    {
        cache.setMaxEntries(maxEntries);
    }

    /**
     * Returns the value to which this map maps the specified key. Returns
     * <tt>null</tt> if the map contains no mapping for this key. A return value
     * of <tt>null</tt> does not <i>necessarily</i> indicate that the map
     * contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to <tt>null</tt>. The <tt>containsKey</tt>
     * operation may be used to distinguish these two cases.
     * 
     * @return the value to which this map maps the specified key.
     * @param key
     *            key whose associated value is to be returned.
     */
    public synchronized Object get(Object key)
    {
        return cache.get(key);
    }

    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for this key, the old value is
     * replaced.
     * 
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @return previous value associated with specified key, or <tt>null</tt> if
     *         there was no mapping for key. A <tt>null</tt> return can also
     *         indicate that the HashMap previously associated <tt>null</tt>
     *         with the specified key.
     */
    public synchronized Object put(Object key, Object value)
    {
        return cache.put(key, value);
    }

    /**
     * Removes the mapping for this key from this map if present.
     * 
     * @param key
     *            key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or <tt>null</tt> if
     *         there was no mapping for key. A <tt>null</tt> return can also
     *         indicate that the map previously associated <tt>null</tt> with
     *         the specified key.
     */
    public synchronized Object remove(Object key)
    {
        return cache.remove(key);
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the specified
     * value.
     * 
     * @param value
     *            value whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map maps one or more keys to the specified
     *         value.
     */
    public synchronized boolean containsValue(Object value)
    {
        return cache.containsValue(value);
    }

    /**
     * Limpa o cache mantendo os parametros configurados
     */
    public synchronized void reset()
    {
        cache.clear();
    }

    public synchronized int size()
    {
        return cache.size();
    }

    /**
     * 
     * @return
     */
    public ArrayList getArrayListValues()
    {
        ArrayList a = null;
        Collection c = this.cache.values();
        a = new ArrayList(c);
        return a;
    }
}

class InternalCache extends LinkedHashMap
{
    private int maxEntries = 0;

    public InternalCache(int maxEntries)
    {
        super(maxEntries, .75f, true);
        this.maxEntries = maxEntries;
    }

    public boolean removeEldestEntry(Map.Entry eldest)
    {
        return size() > maxEntries;
    }

    public void setMaxEntries(int maxEntries)
    {
        if (this.maxEntries <= maxEntries)
        {
            // se aumentar de tamanho, nao limpa o hash
            this.maxEntries = maxEntries;
        }
        else
        {
            // quando diminui o tamanho, limpa o hash
            this.clear();
            this.maxEntries = maxEntries;
        }
    }
}
