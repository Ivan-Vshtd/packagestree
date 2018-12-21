import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author iveshtard
 * @since 12/21/2018
 */
public class FluxToJson {

	public static void main(String[] args) {

		Package aPackage = getNodeFromRecursive(new File("D:\\pack1"));
		Flux.just(aPackage)
				.expand(pack -> Flux.fromIterable(pack.getChildren()))
				.subscribe(FluxToJson::toJSON);
	}

	private static Package getNodeFromRecursive(File file) {
		Package node = new Package(file);
		if (file.isDirectory()) {
			node = new Package(file,
					Arrays.stream(file.listFiles())
							.map(eachFile -> getNodeFromRecursive(eachFile))
							.collect(Collectors.toList())
							.toArray(new Package[]{}));
		}
		return node;
	}

	private static void toJSON(Package pack) {
		ObjectMapper mapper = new ObjectMapper();

		try(OutputStreamWriter ow = new OutputStreamWriter(System.out)) {
			mapper.writeValue(ow, pack);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

final class Package {
	@JsonProperty("package")
	private File name;

	@JsonProperty("children")
	private List<Package> children;

	Package(File name, Package... nodes) {
		this.name = name;
		this.children = new ArrayList<>();
		children.addAll(Arrays.asList(nodes));
	}

	public List<Package> getChildren() {
		return children;
	}
}
