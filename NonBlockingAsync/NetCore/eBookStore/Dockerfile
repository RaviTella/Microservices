FROM microsoft/dotnet:2.2.0-aspnetcore-runtime AS base
COPY bin/Release/netcoreapp2.2/publish/ app/
WORKDIR /app
ENTRYPOINT ["dotnet", "/app/eBookStore.dll"]