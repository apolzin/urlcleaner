package apolzin.unshitify.vo;



import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;




import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;


@PersistenceCapable
public class DomainKey {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent
	private ArrayList<String> bannedKeys;
    @Persistent
    private ArrayList<String> allowedKeys;
    @Persistent
    private String domain;

	public DomainKey(String url)
	{
        domain = normalize(url);
        key = createKey();

	}


    private String normalize(String url)
    {
        return(url.replaceFirst("^(http:\\/\\/)?\\.?(www)?\\.?","").
            replaceFirst("\\/.*$", ""));
    }

    public String getDomain()
    {
        return domain;
    }

    public ArrayList<String> getBannedKeys()
    {
        return bannedKeys;
    }



    public void addToBannedKeys(String key)
    {
        try
        {
            bannedKeys.add(key);
        }
        catch(Exception e)
        {

        }
    }

    public boolean hasBannedKey(String key)
    {
        try
        {
            return bannedKeys.contains(key);
        }
        catch(Exception e)
        {
            return false;
        }

    }

    public void addToAllowedKeys(String key)
    {
        try
        {
            allowedKeys.add(key);
        }
        catch(Exception e)
        {

        }
    }

    public boolean hasAllowedKey(String key)
    {
        try
        {
            return allowedKeys.contains(key);
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public void setBannedKeys(ArrayList<String> keys)
    {
        this.bannedKeys = keys;
    }

	public Key createKey()
	{
        return KeyFactory.createKey(DomainKey.class.getSimpleName(), normalize(this.domain));
	}
}
