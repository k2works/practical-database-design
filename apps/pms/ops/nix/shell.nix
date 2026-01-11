{ packages ? import <nixpkgs> {} }:
let
  rootOpsPath = ../../../../ops/nix;
  baseShell = import "${rootOpsPath}/shells/shell.nix" { inherit packages; };
in
packages.mkShell {
  inherit (baseShell) pure;
  buildInputs = baseShell.buildInputs ++ (with packages; [
    jdk25
    gradle
    jdt-language-server
    nodejs_22
    nodePackages.npm
    httpie
    jq
  ]);

  JAVA_HOME = "${packages.jdk25}";
  APP_NAME = "pms";

  shellHook = ''
    ${baseShell.shellHook}
    echo "PMS (Production Management System) development environment activated"
    echo "  - Java: $(java -version 2>&1 | head -1)"
    echo ""
    echo "Commands: ./gradlew bootRun | test | fullCheck"
  '';
}
