{ packages ? import <nixpkgs> {} }:
let
  baseShell = import ../../shells/shell.nix { inherit packages; };
in
packages.mkShell {
  inherit (baseShell) pure;
  buildInputs = baseShell.buildInputs ++ (with packages; [
    jdk25
    gradle
    jdt-language-server
  ]);

  JAVA_HOME = "${packages.jdk25}";

  shellHook = ''
    ${baseShell.shellHook}
    echo "Java development environment activated"
    echo "  - Java: $(java -version 2>&1 | head -1)"
    echo "  - Gradle: $(gradle --version 2>/dev/null | grep Gradle || echo 'Use ./gradlew')"
  '';
}
